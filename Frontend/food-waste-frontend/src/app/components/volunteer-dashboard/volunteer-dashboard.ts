import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Pickup } from '../../services/pickup';
import { Volunteer } from '../../services/volunteer';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-volunteer-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './volunteer-dashboard.html',
  styleUrl: './volunteer-dashboard.css'
})
export class VolunteerDashboard implements OnInit {

  pickups: any[] = [];
  volunteerProfile: any;
  currentUser: any;

  constructor(
    private pickupService: Pickup,
    private volunteerService: Volunteer,
    private authService: Auth
  ) {}

  ngOnInit(): void {
    this.currentUser = {
      name: localStorage.getItem('name'),
      email: localStorage.getItem('email')
    };

    this.loadPickups();
    this.loadVolunteerProfile();
  }

  loadPickups(): void {
    this.pickupService.getMyPickups().subscribe({
      next: (data: any) => {
        this.pickups = data;
      },
      error: (error: any) => {
        console.error('Error loading pickups:', error);
      }
    });
  }

  loadVolunteerProfile(): void {
    this.volunteerService.getMyVolunteerProfile().subscribe({
      next: (data: any) => {
        this.volunteerProfile = data;
      },
      error: (error: any) => {
        console.error('Error loading volunteer profile:', error);
      }
    });
  }

  get scheduledPickups(): number {
    return this.pickups.filter(
      pickup => pickup.status === 'SCHEDULED'
    ).length;
  }

  get completedPickups(): number {
    return this.pickups.filter(
      pickup => pickup.status === 'COMPLETED'
    ).length;
  }

  get recentPickups(): any[] {
    return this.pickups.slice(0, 5);
  }

  toggleAvailability(): void {
    if (!this.volunteerProfile) {
      return;
    }

    const newValue = !this.volunteerProfile.available;

    this.volunteerService.toggleAvailability().subscribe({
      next: () => {
        this.volunteerProfile.available = newValue;
      },
      error: (error: any) => {
        console.error('Error updating availability:', error);
      }
    });
  }
}