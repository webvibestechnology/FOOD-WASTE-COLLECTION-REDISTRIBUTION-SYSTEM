import { Component } from '@angular/core';
import { ErrorAlert } from '../../shared/error-alert/error-alert';
  import {
    AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';

import { Router, RouterLink } from '@angular/router';
import { Auth } from '../../services/auth';

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

  errorMessage: string = '';
  isLoading: boolean = false;

  registerForm = this.formBuilder.group(
    {
      name: ['', [Validators.required]],

      email: ['', [
        Validators.required,
        Validators.email
      ]],

      phone: [''],

      password: ['', [
        Validators.required,
        Validators.minLength(6)
      ]],

      confirmPassword: ['', [
        Validators.required
      ]],

      role: ['', [
        Validators.required
      ]]
    },
    {
      validators: this.passwordMatchValidator()
    }
  );

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: Auth
  ) {}