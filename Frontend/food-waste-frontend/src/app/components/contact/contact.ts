// @ts-ignore Angular dependencies are resolved by the Angular build environment.
import { Component } from '@angular/core';
// @ts-ignore Angular dependencies are resolved by the Angular build environment.
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule
  ],
  templateUrl: './contact.html',
  styleUrl: './contact.css'
})
export class ContactComponent {

  name: string = '';
  email: string = '';
  subject: string = '';
  message: string = '';

  successMessage: string = '';
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(private http: HttpClient) {}

  onSubmit(): void {
    if (!this.name || !this.email || !this.message) {
      this.errorMessage = 'Please provide your name, email, and message.';
      return;
    }

    this.isLoading = true;
    this.successMessage = '';
    this.errorMessage = '';

    this.http.post<any>('http://localhost:8080/api/auth/contact', {
      name: this.name,
      email: this.email,
      subject: this.subject,
      message: this.message
    }).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.successMessage = res?.message || 'Thank you for contacting us. Your message has been received by platform administrators.';
        this.name = '';
        this.email = '';
        this.subject = '';
        this.message = '';
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err?.error?.message || 'Failed to deliver message. Please try again later.';
      }
    });
  }
}
