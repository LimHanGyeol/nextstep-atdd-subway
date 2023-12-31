package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.exception.LineAlreadyExistException;
import nextstep.subway.line.exception.LineNonExistException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.exception.LineExceptionMessage.EXCEPTION_MESSAGE_EXIST_LINE;
import static nextstep.subway.line.exception.LineExceptionMessage.EXCEPTION_MESSAGE_NON_EXIST_LINE;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        validateLineName(lineRequest.getName());

        Station upStation = stationService.findStationById(lineRequest.getUpStationId());
        Station downStation = stationService.findStationById(lineRequest.getDownStationId());

        Line savedLine = lineRepository.save(lineRequest.toLine());
        savedLine.addSection(upStation, downStation, lineRequest.getDistance(), lineRequest.getDuration());

        return LineResponse.of(savedLine);
    }

    private void validateLineName(String lineName) {
        if (lineRepository.existsByName(lineName)) {
            throw new LineAlreadyExistException(EXCEPTION_MESSAGE_EXIST_LINE);
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLineResponses() {
        return findAllLines().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Line> findAllLines() {
        return lineRepository.findAll();
    }

    @Transactional(readOnly = true)
    public LineResponse findLineResponseById(Long lindId) {
        Line line = findLineById(lindId);
        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new LineNonExistException(EXCEPTION_MESSAGE_NON_EXIST_LINE));
    }

    public LineResponse updateLine(Long lineId, LineRequest lineRequest) {
        Line line = findLineById(lineId);

        line.update(lineRequest.toLine());

        return LineResponse.of(line);
    }

    public void deleteLineById(Long lineId) {
        Line line = findLineById(lineId);

        lineRepository.delete(line);
    }

    public LineResponse addSectionToLine(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findStationById(sectionRequest.getUpStationId());
        Station downStation = stationService.findStationById(sectionRequest.getDownStationId());

        Line line = findLineById(lineId);
        line.addSection(upStation, downStation, sectionRequest.getDistance(), sectionRequest.getDuration());

        Line savedLine = lineRepository.save(line);
        return LineResponse.of(savedLine);
    }

    public void removeSectionToLine(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        Station removeStation = stationService.findStationById(stationId);

        line.removeSection(removeStation);
    }
}
