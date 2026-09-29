import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [
    FormsModule
  ],
  templateUrl: './contact.html',
  styleUrl: './contact.css'
})
export class ContactComponent {

  name: string = '';
  email: string = '';
  message: string = '';

  successMessage: string = '';

  onSubmit(): void {

    this.successMessage =
      'Thank you for contacting us. Your message has been received.';

    this.name = '';
    this.email = '';
    this.message = '';
  }
}