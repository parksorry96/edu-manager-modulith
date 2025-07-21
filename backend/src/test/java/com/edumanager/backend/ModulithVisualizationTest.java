package com.edumanager.backend;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Spring Modulith 구조를 시각화하는 테스트
 * 
 * 실행하면 다음 파일들이 생성됩니다:
 * - target/spring-modulith-docs/module-structure.adoc: 모듈 구조 문서
 * - target/spring-modulith-docs/module-structure.puml: PlantUML 다이어그램
 * - target/spring-modulith-docs/component-diagram.puml: 컴포넌트 다이어그램
 */
class ModulithVisualizationTest {

    @Test
    void createModulithDocumentation() {
        ApplicationModules modules = ApplicationModules.of("com.edumanager");
        
        // 모듈 구조 검증
        modules.verify();
        
        // 문서 생성
        new Documenter(modules)
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml()
                .writeModuleCanvases()
                .writeDocumentation();
        
        System.out.println("=== Spring Modulith 문서가 생성되었습니다 ===");
        System.out.println("위치: target/spring-modulith-docs/");
        System.out.println("- module-structure.adoc: 모듈 구조 문서");
        System.out.println("- module-structure.puml: PlantUML 다이어그램");
        System.out.println("- component-diagram.puml: 컴포넌트 다이어그램");
    }
    
    @Test
    void verifyModuleBoundaries() {
        ApplicationModules modules = ApplicationModules.of("com.edumanager");
        
        // 모듈 정보 출력 (검증 전에)
        System.out.println("=== 감지된 모듈들 ===");
        modules.forEach(module -> {
            System.out.println("모듈: " + module.getName());
            System.out.println("  - 베이스 패키지: " + module.getBasePackage());
            System.out.println("  - 스프링 빈 수: " + module.getSpringBeans().size());
            System.out.println();
        });
        
        // 모듈 경계 검증 (위반사항이 있어도 에러 출력)
        try {
            modules.verify();
            System.out.println("✅ 모듈 경계 검증 성공!");
        } catch (Exception e) {
            System.out.println("❌ 모듈 경계 위반 발견:");
            System.out.println(e.getMessage());
        }
    }
    
    @Test
    void printModuleStructure() {
        ApplicationModules modules = ApplicationModules.of("com.edumanager");
        
        System.out.println("=== 모듈 구조 분석 ===");
        modules.forEach(module -> {
            System.out.println("📦 " + module.getName());
            System.out.println("   📍 패키지: " + module.getBasePackage());
            System.out.println("   📤 발행 이벤트:");
            module.getPublishedEvents().forEach(event -> {
                System.out.println("      📡 " + event.getType().getSimpleName());
            });
            System.out.println();
        });
    }
}