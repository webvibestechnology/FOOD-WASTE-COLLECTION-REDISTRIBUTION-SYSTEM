import { Component } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import { Router } from '@angular/router';
import { Donation } from '../../services/donation';

@Component({
  selector: 'app-food-donation',
  standalone: true,
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './food-donation.html',
  styleUrl: './food-donation.css'
})
export class FoodDonationComponent {

  successMessage: string = '';
  errorMessage: string = '';
  isLoading: boolean = false;

  donationForm = this.formBuilder.group({
    title: ['', Validators.required],

    description: [''],

    quantity: [
      '',
      [
        Validators.required,
        Validators.min(1)
      ]
    ],

    quantityUnit: [
      '',
      Validators.required
    ],

    category: [
      '',
      Validators.required
    ],

    expiryTime: [
      '',
      [
        Validators.required,
        this.futureDateValidator()
      ]
    ]
  });

  constructor(
    private formBuilder: FormBuilder,
    private donationService: Donation,
    private router: Router
  ) {}

  futureDateValidator(): ValidatorFn {
    return (
      control: AbstractControl
    ): ValidationErrors | null => {

      if (!control.value) {
        return null;
      }

      const selectedDate = new Date(control.value);
      const currentDate = new Date();

      if (selectedDate > currentDate) {
        return null;
      }

      return {
        pastDate: true
      };
    };
  }

  onSubmit(): void {

    this.successMessage = '';
    this.errorMessage = '';

    if (this.donationForm.invalid) {
      this.donationForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;

    const formData = this.donationForm.value;

    this.donationService.createDonation(formData).subscribe({

      next: () => {

        this.successMessage =
          'Donation submitted successfully!';

        this.isLoading = false;

        setTimeout(() => {
          this.router.navigate(['/donor/history']);
        }, 2000);
      },

      error: () => {

        this.errorMessage =
          'Failed to submit donation. Please try again.';

        this.isLoading = false;
      }

    });
  }
}