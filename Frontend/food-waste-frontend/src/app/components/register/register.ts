import { Component } from '@angular/core';
import {
<<<<<<< HEAD
=======
  AbstractControl,
>>>>>>> 64475a0 (complete)
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
  AbstractControl,
  ValidationErrors
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Auth } from '../../services/auth';
<<<<<<< HEAD
import { ErrorAlert } from '../../shared error alert/error-alert/error-alert';
=======
import { ErrorAlert } from '../../shared/error-alert/error-alert';
>>>>>>> 64475a0 (complete)

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    ErrorAlert
  ],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class RegisterComponent {

  registerForm: FormGroup;

  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;
<<<<<<< HEAD
=======
  registerForm: FormGroup;
>>>>>>> 64475a0 (complete)

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: Auth
  ) {
<<<<<<< HEAD

    this.registerForm = this.formBuilder.group(
      {
        name: ['', [Validators.required]],
        email: ['', [Validators.required, Validators.email]],
        phone: ['', [Validators.required]],
        password: ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword: ['', [Validators.required]],
        role: ['DONOR', [Validators.required]]
=======
    this.registerForm = this.formBuilder.group(
      {
        name: ['', [Validators.required, Validators.minLength(2)]],

        email: ['', [
          Validators.required,
          Validators.email
        ]],

        phone: ['', [
          this.optionalPhoneValidator()
        ]],

        password: ['', [
          Validators.required,
          Validators.minLength(8),
          this.passwordStrengthValidator()
        ]],

        confirmPassword: ['', [
          Validators.required
        ]],

        role: ['', [
          Validators.required
        ]]
>>>>>>> 64475a0 (complete)
      },
      {
        validators: this.passwordMatchValidator()
      }
    );
  }

<<<<<<< HEAD
  passwordMatchValidator() {
    return (control: AbstractControl): ValidationErrors | null => {

      const password = control.get('password')?.value;
      const confirmPassword = control.get('confirmPassword')?.value;

      if (password !== confirmPassword) {
=======
  optionalPhoneValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value || control.value.trim() === '') {
        return null;
      }
      const isValid = /^[+]?[0-9]{10,15}$/.test(control.value.trim());
      return isValid ? null : { invalidPhone: true };
    };
  }

  passwordMatchValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const password = control.get('password');
      const confirmPassword = control.get('confirmPassword');

      if (!password || !confirmPassword) {
        return null;
      }

      if (password.value !== confirmPassword.value) {
>>>>>>> 64475a0 (complete)
        return { passwordMismatch: true };
      }

      return null;
    };
  }

<<<<<<< HEAD
  onSubmit(): void {

    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
=======
  passwordStrengthValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const password = control.value;
      
      if (!password) {
        return null;
      }

      const hasUpperCase = /[A-Z]/.test(password);
      const hasLowerCase = /[a-z]/.test(password);
      const hasNumber = /\d/.test(password);
      const hasMinLength = password.length >= 8;

      if (!hasUpperCase || !hasLowerCase || !hasNumber || !hasMinLength) {
        return { 
          passwordStrength: true
        };
      }

      return null;
    };
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      this.errorMessage = 'Please fix the highlighted fields before submitting.';
>>>>>>> 64475a0 (complete)
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
<<<<<<< HEAD
    this.successMessage = '';

    const formData = this.registerForm.value;

    this.authService.register(
      formData.name,
      formData.email,
      formData.password,
      formData.phone,
      formData.role
    ).subscribe({
      next: () => {

        this.isLoading = false;
        this.successMessage = 'Registration successful. Please login.';

        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1500);
      },

      error: (error) => {

        this.isLoading = false;

        this.errorMessage =
          error?.error?.message ||
          'Registration failed. Please try again.';
      }
    });
  }
}
=======

    const formData = this.registerForm.value;
    const phoneVal = formData.phone && formData.phone.trim() ? formData.phone.trim() : null;

    this.authService.register(
      formData.name.trim(),
      formData.email.trim(),
      formData.password,
      phoneVal,
      formData.role
    ).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err?.error?.message || err?.message || 'Registration failed. Please try again.';
      }
    });
  }
}
>>>>>>> 64475a0 (complete)
