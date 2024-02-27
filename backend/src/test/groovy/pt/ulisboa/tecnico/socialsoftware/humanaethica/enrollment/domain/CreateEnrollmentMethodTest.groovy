package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;

import java.time.LocalDateTime

@DataJpaTest
class CreateEnrollmentMethodTest extends SpockTest {

    Enrollment enrollment = Mock()
    Volunteer volunteer = Mock()
    Activity activity = Mock()
    def enrollmentDto

    def setup() {
        given: "enrollment info"
        enrollmentDto = new EnrollmentDto()
        enrollmentDto.motivation = ENROLLMENT_MOTIVATION_10
        enrollmentDto.enrollmentDate = DateHandler.toISOString(NOW)
    }

    @Unroll
    def "create enrollment and violate invariant motivation: motivation=#motivation"() {
        given:
        enrollmentDto.motivation = motivation

        when:
        new Enrollment(enrollmentDto, volunteer, activity)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        motivation               || errorMessage
        null                     || ErrorMessage.MOTIVATION_IS_EMPTY
        ENROLLMENT_MOTIVATION_01 || ErrorMessage.MOTIVATION_IS_EMPTY
        ENROLLMENT_MOTIVATION_09 || ErrorMessage.MOTIVATION_TOO_SHORT
    }

  @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}      

}
 
