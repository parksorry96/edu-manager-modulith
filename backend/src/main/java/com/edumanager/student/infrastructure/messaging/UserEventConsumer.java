package com.edumanager.student.infrastructure.messaging;

import com.edumanager.shared.domain.enums.AccountStatus;
import com.edumanager.shared.domain.enums.UserRole;
import com.edumanager.student.domain.entity.Student;
import com.edumanager.student.domain.repository.StudentRepository;
import com.edumanager.user.domain.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {
    private final StudentRepository studentRepository;

    @Transactional
    @KafkaListener(topics = "user.registered", groupId = "student-service-group",containerFactory = "virtualThreadKafkaListenerContainerFactory")
    public void handleUserRegistered(@Payload UserRegisteredEvent event, Acknowledgment ack) {
        try{
            if (event.targetStudentId() != null && event.role() == UserRole.STUDENT) {
                processStudentAccountLinking(event);
            }
            ack.acknowledge();
        } catch (Exception e) {
            throw new RuntimeException("이벤트 로직 실패",e);
        }
    }

    private void processStudentAccountLinking(UserRegisteredEvent event){
        try{
            Student student = studentRepository.findById(event.targetStudentId())
                    .orElseThrow(()-> new IllegalStateException("연결할 학생이 없습니다."));

            if(student.getUserId()!=null){
                return;
            }
            student.linkUserAccount(event.userId());
            student.updateAccountStatus(AccountStatus.LINKED);

            Student savedStudent = studentRepository.save(student);
            publishStudentAccountLinkedEvent(savedStudent,event);
        }catch(Exception e){
            throw e;
        }
    }

    private void publishStudentAccountLinkedEvent(Student student, UserRegisteredEvent originalEvent) {
        // 추후 다른 모듈에서 학생 계정 연결 완료를 알아야 하는 경우
        log.info(" StudentAccountLinkedEvent 발행 준비: studentId={}, userId={}",
                student.getId(), student.getUserId());

        // StudentAccountLinkedEvent 발행 로직
        // eventPublisher.publish(StudentAccountLinkedEvent.of(...));
    }
}
