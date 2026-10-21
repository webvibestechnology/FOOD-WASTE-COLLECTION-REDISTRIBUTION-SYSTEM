import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Ngo } from '../../services/ngo';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-ngo-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './ngo-profile.html',
  styleUrl: './ngo-profile.css'
})
export class NgoProfileComponent implements OnInit {

  profileForm!: FormGroup;
  profile: any = null;
  isEditing = false;
  isLoading = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private ngoService: Ngo,
    private authService: Auth
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadProfile();
  }

  initForm(): void {
    this.profileForm = this.fb.group({
      organizationName: ['', Validators.required],
      registrationNumber: ['', Validators.required],
      description: [''],
      contactPerson: ['', Validators.required],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9+\s-]{7,15}$/)]]
    });
  }

  loadProfile(): void {
    this.isLoading = true;
    this.ngoService.getMyNgoProfile().subscribe({
      next: (data) => {
        this.profile = data;
        this.profileForm.patchValue({
          organizationName: data.organizationName || data.ngoName || data.name || '',
          registrationNumber: data.registrationNumber || '',
          description: data.description || '',
          contactPerson: data.contactPerson || '',
          phone: data.phone || ''
        });
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading NGO profile:', err);
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

    const formValues = {
      ngoName: this.profileForm.value.organizationName,
      registrationNumber: this.profileForm.value.registrationNumber,
      description: this.profileForm.value.description,
      contactPerson: this.profileForm.value.contactPerson,
      phone: this.profileForm.value.phone
    };

    this.ngoService.updateNgoProfile(formValues).subscribe({
      next: (updated) => {
        this.profile = { ...this.profile, ...updated };
        this.isEditing = false;
        this.isLoading = false;
        this.successMessage = 'NGO profile updated successfully!';
        setTimeout(() => { this.successMessage = ''; }, 4000);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err?.error?.message || 'Failed to update profile.';
      }
    });
  }
}
