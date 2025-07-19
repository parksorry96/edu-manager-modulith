package com.edumanager.shared.exception;

public class EntityNotFoundException extends BusinessException{

    // ID로 엔티티를 찾지 못했을 떄 사용하는 생성자
    public EntityNotFoundException(String entity, Long id) {
        super(ErrorCode.ENTITY_NOT_FOUND,
                String.format("%s를 찾을 수 없습니다. ID: %d", entity, id));
    }

    //식별자로 찾지 못했을 때 사용하는 생성자
    public EntityNotFoundException(String entity, String identifier) {
        // 문자열 식별자 버전의 상세 메시지 생성
        super(ErrorCode.ENTITY_NOT_FOUND,
                String.format("%s를 찾을 수 없습니다. 식별자: %s", entity, identifier));
    }

}
