package nextstep.subway.line.domain;

import nextstep.subway.line.exception.CannotAddSectionException;
import nextstep.subway.line.exception.CannotRemoveSectionException;
import nextstep.subway.line.exception.SectionNonExistException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.StationAlreadyExistException;
import nextstep.subway.station.exception.StationNonExistException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.line.exception.LineExceptionMessage.*;
import static nextstep.subway.line.exception.LineExceptionMessage.EXCEPTION_MESSAGE_NON_EXIST_FRONT_SECTION;
import static nextstep.subway.station.exception.StationExceptionMessage.EXCEPTION_MESSAGE_EXIST_STATION_IN_SECTION;
import static nextstep.subway.station.exception.StationExceptionMessage.EXCEPTION_MESSAGE_NON_EXIST_STATION_IN_SECTION;

@Embeddable
public class Sections {

    private static final int SECTION_MIN_SIZE = 1;
    private static final int FIRST_INDEX = 0;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections;

    public Sections() {
        sections = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        boolean existUpStation = isExistStation(section.getUpStation());
        boolean existDownStation = isExistStation(section.getDownStation());

        validateAddableSection(existUpStation, existDownStation);

        if (existUpStation) {
            addBackwardSection(section);
        }
        if (existDownStation) {
            addForwardSection(section);
        }
    }

    private boolean isExistStation(Station station) {
        return sections.stream()
                .anyMatch(section -> isEqualsStation(section, station));
    }

    private boolean isEqualsStation(Section section, Station station) {
        return Objects.equals(section.getUpStation(), station) || Objects.equals(section.getDownStation(), station);
    }

    private void validateAddableSection(boolean existUpStation, boolean existDownStation) {
        if (existUpStation && existDownStation) {
            throw new StationAlreadyExistException(EXCEPTION_MESSAGE_EXIST_STATION_IN_SECTION);
        }
        if (!existUpStation && !existDownStation) {
            throw new StationNonExistException(EXCEPTION_MESSAGE_NON_EXIST_STATION_IN_SECTION);
        }
    }

    private void addBackwardSection(Section section) {
        Section existingSection = findSectionByUpStation(section.getUpStation())
                .orElseGet(() -> null);

        if (Objects.isNull(existingSection)) {
            sections.add(section);
            return;
        }

        int existingDistance = existingSection.getDistance();
        int existingDuration = existingSection.getDuration();
        int newSectionDistance = section.getDistance();
        int newSectionDuration = section.getDuration();

        validateSectionDistance(existingDistance, newSectionDistance);
        validateSectionDuration(existingDuration, newSectionDuration);

        int existingSectionIndex = sections.indexOf(existingSection);
        int adjustedDistance = calculateNewDistance(existingDistance, newSectionDistance);
        int adjustedDutation = calculateNewDuration(existingDuration, newSectionDuration);

        Line existingLine = section.getLine();
        Station existingUpStation = existingSection.getUpStation();
        Station existingDownStation = existingSection.getDownStation();
        Station newDownStation = section.getDownStation();

        sections.set(existingSectionIndex, new Section(existingLine, existingUpStation, newDownStation, newSectionDistance, newSectionDuration));
        sections.add(existingSectionIndex + SECTION_MIN_SIZE, new Section(existingLine, newDownStation, existingDownStation, adjustedDistance, adjustedDutation));
    }

    private void addForwardSection(Section section) {
        Section existingSection = findSectionByDownStation(section.getDownStation())
                .orElseGet(() -> null);

        if (Objects.isNull(existingSection)) {
            sections.add(section);
            return;
        }

        int existingDistance = existingSection.getDistance();
        int existingDuration = existingSection.getDuration();
        int newSectionDistance = section.getDistance();
        int newSectionDuration = section.getDuration();

        validateSectionDistance(existingDistance, newSectionDistance);
        validateSectionDuration(existingDuration, newSectionDuration);

        int existingSectionIndex = sections.indexOf(existingSection);
        int adjustedDistance = calculateNewDistance(existingDistance, newSectionDistance);
        int adjustedDuration = calculateNewDuration(existingDuration, newSectionDuration);

        Station existingUpStation = existingSection.getUpStation();
        Station newUpStation = section.getUpStation();

        sections.set(existingSectionIndex, section);
        sections.add(existingSectionIndex, new Section(section.getLine(), existingUpStation, newUpStation, adjustedDistance, adjustedDuration));
    }

    private Optional<Section> findSectionBy(Predicate<Section> predicate) {
        return sections.stream()
                .filter(predicate)
                .findFirst();
    }

    private Optional<Section> findSectionByUpStation(Station upStation) {
        return findSectionBy(section -> section.getUpStation().equals(upStation));
    }

    private Optional<Section> findSectionByDownStation(Station downStation) {
        return findSectionBy(section -> section.getDownStation().equals(downStation));
    }

    private void validateSectionDistance(int findSectionDistance, int newSectionDistance) {
        if (findSectionDistance <= newSectionDistance) {
            throw new CannotAddSectionException(EXCEPTION_MESSAGE_INVALID_SECTION_DISTANCE);
        }
    }

    private void validateSectionDuration(int findSectionDuration, int newSectionDuration) {
        if (findSectionDuration <= newSectionDuration) {
            throw new CannotAddSectionException(EXCEPTION_MESSAGE_INVALID_SECTION_DURATION);
        }
    }

    private int calculateNewDistance(int findSectionDistance, int newSectionDistance) {
        return findSectionDistance - newSectionDistance;
    }

    private int calculateNewDuration(int findSectionDuration, int newSectionDuration) {
        return findSectionDuration - newSectionDuration;
    }

    public void removeSection(Station deleteStation) {
        validateDeletableCurrentSections();

        if (getFirstUpStation().equals(deleteStation)) {
            sections.remove(getFirstSection());
            return;
        }
        if (getLastDownStation().equals(deleteStation)) {
            sections.remove(getLastSection());
            return;
        }

        deleteMiddleSection(deleteStation);
    }

    private void validateDeletableCurrentSections() {
        if (sections.size() == SECTION_MIN_SIZE || sections.isEmpty()) {
            throw new CannotRemoveSectionException(EXCEPTION_MESSAGE_NOT_DELETABLE_SECTION);
        }
    }

    private Station getFirstUpStation() {
        Section section = getFirstSection();
        return section.getUpStation();
    }

    private Section getFirstSection() {
        return sections.get(FIRST_INDEX);
    }

    private Station getLastDownStation() {
        Section section = getLastSection();
        return section.getDownStation();
    }

    private Section getLastSection() {
        return sections.get(sections.size() - SECTION_MIN_SIZE);
    }

    private void deleteMiddleSection(Station deleteStation) {
        Section frontSection = getFrontSection(deleteStation);
        Section backSection = getBackSection(deleteStation);

        Station newUpStation = frontSection.getUpStation();
        Station newDownStation = backSection.getDownStation();
        int newDistance = frontSection.getDistance() + backSection.getDistance();
        int newDuration = frontSection.getDuration() + backSection.getDuration();

        Line existingLine = frontSection.getLine();
        Section newSection = new Section(existingLine, newUpStation, newDownStation, newDistance, newDuration);

        sections.remove(frontSection);
        sections.remove(backSection);
        sections.add(newSection);
    }

    private Section getFrontSection(Station deleteStation) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(deleteStation))
                .findFirst()
                .orElseThrow(() -> new SectionNonExistException(EXCEPTION_MESSAGE_NON_EXIST_BACK_SECTION));
    }

    private Section getBackSection(Station deleteStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(deleteStation))
                .findFirst()
                .orElseThrow(() -> new SectionNonExistException(EXCEPTION_MESSAGE_NON_EXIST_FRONT_SECTION));
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public List<Line> getSectionContainsLines() {
        List<Line> lines = sections.stream()
                .map(Section::getLine)
                .collect(Collectors.toList());

        return Collections.unmodifiableList(lines);

    }

    public List<Station> getStations() {
        return sections.stream()
                .sorted()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    public int getTotalDistance() {
        return sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }

    public int getTotalDuration() {
        return sections.stream()
                .mapToInt(Section::getDuration)
                .sum();
    }
}
