import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Volunteer } from '../../services/volunteer';

@Component({
  selector: 'app-volunteer-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './volunteer-profile.html',
  styleUrl: './volunteer-profile.css'
})
export class VolunteerProfileComponent implements OnInit {

  profileForm!: FormGroup;
  profile: any = null;
  isEditing = false;
  isLoading = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private volunteerService: Volunteer
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadProfile();
  }

  initForm(): void {
    this.profileForm = this.fb.group({
      phone: ['', [Validators.required, Validators.pattern(/^[0-9+\s-]{7,15}$/)]],
      vehicleType: ['', Validators.required],
      operatingArea: ['', Validators.required],
      available: [true]
    });
  }

  loadProfile(): void {
    this.isLoading = true;
    this.volunteerService.getMyVolunteerProfile().subscribe({
      next: (data) => {
        this.profile = data;
        this.profileForm.patchValue({
          phone: data.phone || data.user?.phone || '',
          vehicleType: data.vehicleType || '',
          operatingArea: data.operatingArea || '',
          available: data.available !== false
        });
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading volunteer profile:', err);
        this.isLoading = false;
      }
    });
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing;
    this.successMessage = '';
    this.errorMessage = '';
  }

  onSave(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.successMessage = '';
    this.errorMessage = '';

    this.volunteerService.updateProfile(this.profileForm.value).subscribe({
      next: (updated) => {
        this.profile = { ...this.profile, ...updated };
        this.isEditing = false;
        this.isLoading = false;
        this.successMessage = 'Volunteer operational profile updated successfully!';
        setTimeout(() => { this.successMessage = ''; }, 4000);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err?.error?.message || 'Failed to update profile.';
      }
    });
  }
}
