import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { CandidateDTO } from '../../models/user-DTOS/candidate.dto';

@Component({
  selector: 'app-register-candidate',
  templateUrl: './register-candidate.component.html',
  styleUrls: ['./register-candidate.component.css'],
  standalone: false
})
export class RegisterCandidateComponent {
  candidate: CandidateDTO = {
    email: '',
    password: '',
    fullName: '',
    profile: '',
    diploma: '',
    phone: '',
    yearsOfExperience: 0,
    technologies: [] as string[],
    interestedProfiles: [] as string[]
  };
  interestedProfilesInput: string = '';
  technologiesInput: string = '';
  errorMessage = '';
  successMessage = '';
  hidePassword = true;

  constructor(private userService: UserService, private router: Router) {}

  resetForm() {
    this.candidate = {
      email: '',
      password: '',
      fullName: '',
      profile: '',
      diploma: '',
      phone: '',
      yearsOfExperience: 0,
      technologies: [] as string[],
      interestedProfiles: [] as string[]
    };
    this.interestedProfilesInput = '';
    this.technologiesInput = '';
  }

  onRegister() {
    this.candidate.technologies = this.technologiesInput.split(',').map(t => t.trim()).filter(t => t);
    this.candidate.interestedProfiles = this.interestedProfilesInput.split(',').map(p => p.trim()).filter(p => p);

    this.userService.registerCandidate(this.candidate).subscribe({
      next: (response) => {
        console.log('Registration successful:', response);
        this.successMessage = 'Candidate registered successfully!';
        this.errorMessage = '';

        // Clear the form
        this.resetForm();

        // Redirect to login page after showing success message
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Registration error:', err);

        // Check if it's actually a success case mishandled as error
        if (err.status === 201 || err.status === 200) {
          // Sometimes HttpClient treats non-JSON responses as errors
          this.successMessage = 'Candidate registered successfully!';
          this.errorMessage = '';
          this.resetForm();
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 2000);
          return;
        }

        // Handle actual errors
        let errorMsg = 'Registration failed';

        if (err.error) {
          // Check if it's our new structured error response
          if (err.error.message) {
            errorMsg = err.error.message;
          } else if (err.error.error) {
            errorMsg = err.error.error;
          } else if (typeof err.error === 'string') {
            errorMsg = err.error;
          } else {
            // Fallback for unstructured errors
            errorMsg = JSON.stringify(err.error);
          }
        } else if (err.message) {
          errorMsg = err.message;
        } else if (typeof err === 'string') {
          errorMsg = err;
        }

        this.errorMessage = errorMsg;
        this.successMessage = '';
      }
    });
  }
}
