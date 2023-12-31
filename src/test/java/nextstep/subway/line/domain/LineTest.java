package nextstep.subway.line.domain;

import nextstep.subway.line.exception.CannotAddSectionException;
import nextstep.subway.line.exception.CannotRemoveSectionException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.StationAlreadyExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("노선 Domain 단위 테스트")
public class LineTest {

    private Station savedStationGyoDae;
    private Station savedStationGangNam;
    private Station savedStationYeokSam;
    private Station savedStationSeolleung;
    private Station savedStationSamSeong;
    private Line line2;

    @BeforeEach
    void setUp() {
        savedStationGangNam = new Station(1L, "강남역");
        savedStationYeokSam = new Station(2L, "역삼역");
        savedStationSamSeong = new Station(3L, "삼성역");
        savedStationGyoDae = new Station(4L, "교대역");
        savedStationSeolleung = new Station(5L, "선릉역");

        line2 = new Line(1L, "2호선", "bg-green-600", 0);
        line2.addSection(savedStationGangNam, savedStationYeokSam, 5, 5);
    }

    @Test
    @DisplayName("노선의 구간에 있는 역들을 가져오기")
    void getStations() {
        // when
        List<Station> stations = line2.getStations();

        // then
        assertThat(stations).hasSize(2);
    }

    @Test
    @DisplayName("노선에 신규 상행역 구간 추가. 교대 - 강남 - 역삼")
    void addSectionInUp() {
        // when
        line2.addSection(savedStationGyoDae, savedStationGangNam, 7, 7);

        // then
        assertThat(line2.getStations()).containsExactlyElementsOf(Arrays.asList(savedStationGyoDae, savedStationGangNam, savedStationYeokSam));
    }

    @Test
    @DisplayName("노선 중간에 신규 구간 추가. 강남 - 역삼 - 선릉 - 삼성")
    void addSectionInMiddle() {
        // given
        line2.addSection(savedStationYeokSam, savedStationSamSeong, 8, 8);

        // when
        line2.addSection(savedStationYeokSam, savedStationSeolleung, 4, 4);

        // then
        assertThat(line2.getStations()).containsExactlyElementsOf(Arrays.asList(savedStationGangNam, savedStationYeokSam, savedStationSeolleung, savedStationSamSeong));
    }

    @Test
    @DisplayName("노선의 하행역에 신규 구간 추가. 강남 - 역삼 - 삼성")
    void addSectionInDown() {
        // when
        line2.addSection(savedStationYeokSam, savedStationSamSeong, 8, 8);

        // then
        assertThat(line2.getStations()).containsExactlyElementsOf(Arrays.asList(savedStationGangNam, savedStationYeokSam, savedStationSamSeong));
    }

    @Test
    @DisplayName("노선 중간에 신규 구간 추가시 소요 시간이 기존 노선 소요 시간보다 크면 Exception 발생")
    void addSectionInMiddleError() {
        // when & then
        assertThatExceptionOfType(CannotAddSectionException.class)
                .isThrownBy(() -> line2.addSection(savedStationGangNam, savedStationSamSeong, 15, 15));
    }

    @Test
    @DisplayName("노선에 존재하는 구간 추가 시 에러 발생")
    void addSectionAlreadyIncluded() {
        assertThatExceptionOfType(StationAlreadyExistException.class)
                .isThrownBy(() -> line2.addSection(savedStationGangNam, savedStationYeokSam, 5, 5));
    }

    @Test
    @DisplayName("노선에 있는 상행 종점역 구간 제거")
    void removeUpStationSection() {
        // given
        line2.addSection(savedStationYeokSam, savedStationSamSeong, 8, 8);

        // when
        line2.removeSection(savedStationGangNam);

        // then
        assertThat(line2.getSections()).hasSize(1);
        assertThat(line2.getStations()).containsExactlyElementsOf(Arrays.asList(savedStationYeokSam, savedStationSamSeong));
    }

    @Test
    @DisplayName("노선에 있는 중간 구간 제거")
    void removeMiddleSection() {
        // given
        line2.addSection(savedStationYeokSam, savedStationSamSeong, 8, 8);
        line2.addSection(savedStationYeokSam, savedStationSeolleung, 4, 4);

        // when
        line2.removeSection(savedStationYeokSam);

        // then
        assertThat(line2.getSections()).hasSize(2);
        assertThat(line2.getStations()).containsExactlyElementsOf(Arrays.asList(savedStationGangNam, savedStationSeolleung, savedStationSamSeong));
        assertThat(line2.findSection(savedStationGangNam, savedStationSeolleung).getDistance()).isEqualTo(9);
    }

    @Test
    @DisplayName("노선에 있는 하행 종점역 구간 제거")
    void removeDownStationSection() {
        // given
        line2.addSection(savedStationYeokSam, savedStationSamSeong, 8, 8);

        // when
        line2.removeSection(savedStationSamSeong);

        // then
        assertThat(line2.getSections()).hasSize(1);
        assertThat(line2.getStations()).containsExactlyElementsOf(Arrays.asList(savedStationGangNam, savedStationYeokSam));
    }

    @Test
    @DisplayName("구간이 하나인 노선 삭제 시 에러 발생")
    void removeSectionNotEndOfList() {
        assertThatExceptionOfType(CannotRemoveSectionException.class)
                .isThrownBy(() -> line2.removeSection(savedStationYeokSam));
    }
}
