package nextstep.subway.line.acceptance.linesection;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import nextstep.subway.line.acceptance.documentation.LineSectionDocumentation;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.utils.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.RestDocumentationContextProvider;

import java.util.Arrays;

import static nextstep.subway.line.acceptance.line.LineRequestSteps.지하철_노선_생성_요청;
import static nextstep.subway.line.acceptance.linesection.LineSectionRequestSteps.*;
import static nextstep.subway.line.acceptance.linesection.LineSectionVerificationSteps.*;
import static nextstep.subway.utils.BaseDocumentation.givenDefault;

@DisplayName("지하철 노선에 역 등록 관련 기능")
public class LineSectionAcceptanceTest extends AcceptanceTest {

    private static final String DOCUMENT_IDENTIFIER_LINE_SECTION = "linesection/{method-name}";

    @BeforeEach
    public void init(RestDocumentationContextProvider restDocumentation) {
        super.setUp(restDocumentation);

        // given
        LineRequest 신분당선_생성_요청 = 노선_요청("신분당선", "bg-red-600", 양재역.getId(), 청계산입구역.getId(), 7, 10);
        신분당선_생성_요청.addExtraCharge(900);

        신분당선 = 지하철_노선_생성_요청(givenDefault(), 신분당선_생성_요청).as(LineResponse.class);
    }

    @Test
    @DisplayName("지하철 노선에 등록된 구간에 새로운 상행 역을 등록한다.")
    void addUpStationLineSection() {
        // given
        baseDocumentation = new LineSectionDocumentation(spec);
        RequestSpecification 지하철_노선_구간_생성_문서화_요청 = baseDocumentation.requestDocumentOfAllType(DOCUMENT_IDENTIFIER_LINE_SECTION);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(지하철_노선_구간_생성_문서화_요청, 신분당선.getId(), 강남역.getId(), 양재역.getId(), 5, 5);

        // then
        지하철_노선에_구간_등록_됨(response);
    }

    @Test
    @DisplayName("지하철 노선에 등록된 구간 사이에 역을 추가 등록한다.")
    void addBetweenLineSection() {
        // when
        지하철_노선에_구간_등록_요청(givenDefault(), 신분당선.getId(), 양재역.getId(), 양재시민의숲역.getId(), 3, 3);
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(givenDefault(), 신분당선.getId(), 판교역.getId(), 양재시민의숲역.getId(), 1, 1);

        // then
        지하철_노선에_구간_등록_됨(response);
        지하철_노선에_등록된_구간_이름_확인(response, Arrays.asList(양재역, 판교역, 양재시민의숲역, 청계산입구역));
    }

    @Test
    @DisplayName("지하철 노선에 새로운 하행 구간을 등록한다.")
    void addDownStationLineSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(givenDefault(), 신분당선.getId(), 청계산입구역.getId(), 판교역.getId(), 16, 13);

        // then
        지하철_노선에_구간_등록_됨(response);
    }

    @Test
    @DisplayName("지하철 노선에 추가하는 구간의 거리가 기존노선의 거리보다 크면 등록할 수 없다.")
    void invalidAddSectionBecauseDistanceLarger() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(givenDefault(), 신분당선.getId(), 양재역.getId(), 양재시민의숲역.getId(), 10, 10);

        // then
        지하철_노선에_구간_등록_실패_됨(response);
    }

    @Test
    @DisplayName("지하철 노선에 추가하는 구간의 거리가 기존노선의 거리보다 같으면 등록할 수 없다.")
    void invalidAddSectionBecauseDistanceEquals() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(givenDefault(), 신분당선.getId(), 양재역.getId(), 양재시민의숲역.getId(), 7, 3);

        // then
        지하철_노선에_구간_등록_실패_됨(response);
    }

    @Test
    @DisplayName("지하철 노선에 추가하는 구간의 상행역과 하행역이 모두 존재하면 등록할 수 없다.")
    void invalidAddSectionBecauseExistStation() {
        // given
        지하철_노선에_구간_등록_요청(givenDefault(), 신분당선.getId(), 청계산입구역.getId(), 판교역.getId(), 16, 13);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(givenDefault(), 신분당선.getId(), 양재역.getId(), 판교역.getId(), 23, 23);

        // then
        지하철_노선에_구간_등록_실패_됨(response);
    }

    @Test
    @DisplayName("지하철 노선에 이미 포함된 역을 구간으로 등록할 수 없다.")
    void invalidAddLineSectionAlreadyIncluded() {
        // given
        지하철_노선에_구간_등록_요청(givenDefault(), 신분당선.getId(), 양재역.getId(), 청계산입구역.getId(), 7, 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(givenDefault(), 신분당선.getId(), 양재역.getId(), 청계산입구역.getId(), 7, 10);

        // then
        지하철_노선에_구간_등록_실패_됨(response);
    }

    @Test
    @DisplayName("지하철 노선에 등록된 상행 종점역을 제거한다.")
    void removeUpStationLineSection() {
        // given
        baseDocumentation = new LineSectionDocumentation(spec);
        RequestSpecification 지하철_노선_구간_제거_문서화_요청 = baseDocumentation.requestDocumentOfDefault(DOCUMENT_IDENTIFIER_LINE_SECTION);

        지하철_노선에_구간_등록_요청(givenDefault(), 신분당선.getId(), 양재역.getId(), 양재시민의숲역.getId(), 3, 3);

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_등록된_구간_제거_요청(지하철_노선_구간_제거_문서화_요청, 신분당선.getId(), 양재역.getId());

        // then
        지하철_노선에_등록된_구간_제거_됨(deleteResponse);
    }

    @Test
    @DisplayName("지하철 노선에 등록된 중간 노선 역을 제거한다.")
    void removeMiddleStationLineSection() {
        // given
        지하철_노선에_구간_등록_요청(givenDefault(), 신분당선.getId(), 청계산입구역.getId(), 판교역.getId(), 16, 13);

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_등록된_구간_제거_요청(givenDefault(), 신분당선.getId(), 청계산입구역.getId());

        // then
        지하철_노선에_등록된_구간_제거_됨(deleteResponse);
    }

    @Test
    @DisplayName("지하철 노선에 등록된 하행 종점역을 제거한다.")
    void removeDownStationLineSection() {
        // given
        지하철_노선에_구간_등록_요청(givenDefault(), 신분당선.getId(), 청계산입구역.getId(), 판교역.getId(), 16, 13).as(LineResponse.class);

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_등록된_구간_제거_요청(givenDefault(), 신분당선.getId(), 판교역.getId());

        // then
        지하철_노선에_등록된_구간_제거_됨(deleteResponse);
    }

    @Test
    @DisplayName("지하철 노선에 구간이 하나혹은 없을때 지하철역을 제거 할 수 없다.")
    void removeLineSectionOnlyOneSection() {
        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_등록된_구간_제거_요청(givenDefault(), 신분당선.getId(), 청계산입구역.getId());

        // then
        지하철_노선에_등록된_구간_제거_실패_됨(deleteResponse);
    }
}
