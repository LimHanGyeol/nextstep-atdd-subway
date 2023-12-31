package nextstep.subway.line.acceptance.documentation;

import io.restassured.specification.RequestSpecification;
import nextstep.subway.utils.BaseDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.restdocs.request.RequestParametersSnippet;

import static nextstep.subway.line.acceptance.documentation.LineDocumentation.initDocumentCommonResponseBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

public class LineSectionDocumentation extends BaseDocumentation {

    public LineSectionDocumentation(RequestSpecification spec) {
        super(spec);
    }

    @Override
    public PathParametersSnippet initDocumentRequestPathVariable() {
        return pathParameters(
                parameterWithName("lineId").description("노선 ID")
        );
    }

    @Override
    public RequestParametersSnippet initDocumentRequestParameters() {
        return requestParameters();
    }

    @Override
    public RequestFieldsSnippet initDocumentRequestBody() {
        return requestFields(
                fieldWithPath("upStationId").type(JsonFieldType.NUMBER).description("상행역 ID"),
                fieldWithPath("downStationId").type(JsonFieldType.NUMBER).description("하행역 ID"),
                fieldWithPath("distance").type(JsonFieldType.NUMBER).description("구간 거리"),
                fieldWithPath("duration").type(JsonFieldType.NUMBER).description("구간 소요 시간")
        );
    }

    @Override
    public ResponseFieldsSnippet initDocumentResponseBody() {
        return initDocumentCommonResponseBody();
    }
}
