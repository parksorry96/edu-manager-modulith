/**
 * Shared 모듈 (공통 모듈)
 * 
 * 모든 모듈에서 공통으로 사용하는 도메인 객체, 이벤트, 유틸리티를 제공합니다.
 * 다른 모듈들이 이 모듈에 의존할 수 있는 공통 기반 모듈입니다.
 * 
 * @author EduManager Team
 */
@ApplicationModule(type = ApplicationModule.Type.OPEN)
package com.edumanager.shared;

import org.springframework.modulith.ApplicationModule;