import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [
    FormsModule,
    RouterLink,
    CommonModule
  ],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css'
})
export class ForgotPasswordComponent {

  email: string = '';
  message: string = '';
  isLoading: boolean = false;

  constructor(private http: HttpClient) {}

  onSubmit(): void {
    if (!this.email) {
      return;
    }

    this.isLoading = true;
    this.message = '';

    this.http.post<any>('http://localhost:8080/api/auth/forgot-password', {
      email: this.email
    }).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.message = res?.message || 'If this email exists, password reset instructions have been logged.';
      },
      error: () => {
        this.isLoading = false;
        // Do not leak email existence for security
        this.message = 'If this email exists, password reset instructions have been logged.';
      }
    });
  }
}
