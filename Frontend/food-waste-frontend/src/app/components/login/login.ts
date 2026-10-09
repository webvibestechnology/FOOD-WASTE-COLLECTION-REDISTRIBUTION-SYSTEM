import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Auth } from '../../services/auth';
import { ErrorAlert } from '../../shared error alert/error-alert/error-alert';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    ErrorAlert
  ],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {

  loginForm!: FormGroup;

  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: Auth
  ) {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {

    // Check form validation
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const email = this.loginForm.value.email!;
    const password = this.loginForm.value.password!;

    this.authService.login(email, password).subscribe({

      next: (response) => {

        // Save token and user information
        this.authService.saveSession(
          response.token,
          response
        );

        // Get user role
        const role = response.role;

        // Navigate according to role
        if (role === 'DONOR') {
          this.router.navigate(['/donor/dashboard']);

        } else if (role === 'NGO') {
          this.router.navigate(['/ngo/dashboard']);

        } else if (role === 'VOLUNTEER') {
          this.router.navigate(['/volunteer/dashboard']);

        } else if (role === 'ADMIN') {
          this.router.navigate(['/admin/dashboard']);

        } else {
          this.router.navigate(['/']);
        }

        this.isLoading = false;
      },

      error: () => {
        this.errorMessage = 'Invalid email or password';
        this.isLoading = false;
      }

    });
  }
}

