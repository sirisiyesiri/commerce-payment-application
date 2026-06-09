package com.example.commercepaymentapplication.domain.portone.dto;

/*
 * PortOne 결제창 초기화에 필요한 공개 설정 값을 전달하는 응답 DTO
 *
 * 프론트엔드에 전달되는 응답이므로, apiSecret 같은 비밀 키는 절대 노출하지 않는다.
 */
public record PortOneConfigResponse(
        String storeId,
        String channelKey
) {
    // PortOne 공개 설정 값으로 응답 DTO를 생성한다.
    public static PortOneConfigResponse of(String storeId, String channelKey) {
        return new PortOneConfigResponse(storeId, channelKey);
    }
}