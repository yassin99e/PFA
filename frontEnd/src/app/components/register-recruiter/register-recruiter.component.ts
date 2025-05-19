import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { RecruiterDTO } from '../../models/user-DTOS/recruiter.dto';

@Component({
  selector: 'app-register-recruiter',
  templateUrl: './register-recruiter.component.html',
  styleUrls: ['./register-recruiter.component.css'],
  standalone: false
})
export class RegisterRecruiterComponent {
  recruiter: RecruiterDTO = {
    email: '',
    password: '',
    fullName: '',
    company: '',
    department: ''
  };

  errorMessage = '';
  successMessage = '';
  hidePassword = true;

  constructor(private userService: UserService, private router: Router) {}

  resetForm() {
    this.recruiter = {
      email: '',
      password: '',
      fullName: '',
      company: '',
      department: ''
    };
  }

  onRegister() {
    this.userService.registerRecruiter(this.recruiter).subscribe({
      next: (response) => {
        console.log('Registration successful:', response);
        this.successMessage = 'Recruiter registered successfully!';
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
          this.successMessage = 'Recruiter registered successfully!';
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
