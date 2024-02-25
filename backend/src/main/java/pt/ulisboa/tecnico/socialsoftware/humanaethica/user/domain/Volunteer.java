package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue(User.UserTypes.VOLUNTEER)
public class Volunteer extends User {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "volunteer", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();


    @OneToMany(mappedBy = "volunteer", fetch =  FetchType.EAGER)
    private List<Assessment> assessments = new ArrayList<>();

    @OneToMany(mappedBy = "volunteer", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Participation> participations = new ArrayList<>();


    public Volunteer() {
    }

    public Volunteer(String name, String username, String email, AuthUser.Type type, State state) {
        super(name, username, email, Role.VOLUNTEER, type, state);
    }

    public Volunteer(String name, State state) {
        super(name, Role.VOLUNTEER, state);
    }

    //Assessments
    
    public List<Assessment> getAssessments() {
        return assessments;
    }

    public void addAssessment(Assessment assessment) { this.assessments.add(assessment);}

    public void removeAssessment(Assessment assessment) { this.assessments.remove(assessment); }

    // Participation

    public List<Participation> getParticipations() {
        return participations;
    }

    public void addParticipation(Participation participation) {
        participations.add(participation);
    }

    public void removeParticipation(Participation participation) {
        participations.remove(participation);
    }
    

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
    }
}
