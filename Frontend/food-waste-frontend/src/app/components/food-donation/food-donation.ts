// Angular dependencies are resolved by the project build; suppress an editor
// diagnostic when dependencies have not yet been installed locally.
// @ts-ignore TS2307
import { Component } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Donation } from '../../services/donation';
<<<<<<< HEAD
import { ErrorAlert } from '../../shared error alert/error-alert/error-alert';
=======
import { ErrorAlert } from '../../shared/error-alert/error-alert';
>>>>>>> 64475a0 (complete)

@Component({
  selector: 'app-food-donation',
  standalone: true,
  imports: [
    ReactiveFormsModule,
<<<<<<< HEAD
=======
    CommonModule,
>>>>>>> 64475a0 (complete)
    ErrorAlert
  ],
  templateUrl: './food-donation.html',
  styleUrls: ['./food-donation.css']
})
export class FoodDonationComponent {

  successMessage: string = '';
  errorMessage: string = '';
  isLoading: boolean = false;
  donationForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private donationService: Donation,
    private router: Router
  ) {
    this.donationForm = this.formBuilder.group({
      title: ['', Validators.required],
<<<<<<< HEAD

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
=======
      description: [''],
      quantity: ['', [Validators.required, Validators.min(1)]],
      quantityUnit: ['', Validators.required],
      category: ['', Validators.required],
      expiryTime: ['', [Validators.required, this.futureDateValidator()]],
      preparationTime: [''],
      safeUseDeadline: ['', [Validators.required, this.futureDateValidator()]],
      allergens: [''],
      storageRequirements: ['Ambient', Validators.required],
      dietaryInfo: ['Vegetarian', Validators.required],
      pickupAddress: ['', Validators.required],
      contactPhone: ['', [Validators.required, Validators.pattern(/^[0-9+\s-]{7,15}$/)]]
>>>>>>> 64475a0 (complete)
    });
  }

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
        this.successMessage = 'Food donation submitted successfully with food safety details!';
        this.isLoading = false;

        setTimeout(() => {
          this.router.navigate(['/donor/history']);
        }, 1500);
      },
      error: (err: any) => {
        this.errorMessage = err?.error?.message || 'Failed to submit donation. Please check food safety fields and try again.';
        this.isLoading = false;
      }
    });
  }
}
