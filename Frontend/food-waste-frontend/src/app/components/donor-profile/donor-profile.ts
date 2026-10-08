```typescript
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { Auth } from '../../services/auth';
import { User } from '../../services/user';

@Component({
  selector: 'app-donor-profile',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './donor-profile.html',
  styleUrl: './donor-profile.css'
})
export class DonorProfile implements OnInit {

  currentUser: any = null;
  isEditing = false;
  successMessage = '';

  editForm!: FormGroup;

  constructor(
    private authService: Auth,
    private userService: User,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {

    // Get currently logged-in user
    this.currentUser = this.authService.getCurrentUser();

    // Create edit form
    this.editForm = this.fb.group({
      name: [
        this.currentUser?.name || '',
        Validators.required
      ],

      phone: [
        this.currentUser?.phone || ''
      ]
    });
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing;

    if (this.isEditing) {
      this.successMessage = '';

      this.editForm.patchValue({
        name: this.currentUser?.name || '',
        phone: this.currentUser?.phone || ''
      });
    }
  }

  onSave(): void {

    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      return;
    }

    this.userService
      .updateUser(this.currentUser.id, this.editForm.value)
      .subscribe({
        next: (updatedUser: any) => {

          // Update current user
          this.currentUser = {
            ...this.currentUser,
            ...updatedUser
          };

          // Update localStorage
          localStorage.setItem(
            'user',
            JSON.stringify(this.currentUser)
          );

          this.successMessage =
            'Profile updated successfully!';

          this.isEditing = false;
        },

        error: (error) => {

          console.error(
            'Error updating profile:',
            error
          );

          this.successMessage =
            'Failed to update profile. Please try again.';
        }
      });
  }

  cancelEdit(): void {
    this.isEditing = false;

    this.editForm.patchValue({
      name: this.currentUser?.name || '',
      phone: this.currentUser?.phone || ''
    });

    this.successMessage = '';
  }
}
```
