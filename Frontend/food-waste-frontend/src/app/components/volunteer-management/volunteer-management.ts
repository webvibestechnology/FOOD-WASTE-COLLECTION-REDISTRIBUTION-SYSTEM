import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Volunteer } from '../../services/volunteer';

@Component({
  selector: 'app-volunteer-management',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './volunteer-management.html',
  styleUrl: './volunteer-management.css'
})
export class VolunteerManagement implements OnInit {

  volunteers: any[] = [];
  filterAvailable = 'ALL';

  constructor(
    private volunteerService: Volunteer
  ) {}

  ngOnInit(): void {
    this.loadVolunteers();
  }

  loadVolunteers(): void {
    this.volunteerService.getAvailableVolunteers().subscribe({
      next: (data: any) => {
        this.volunteers = data;
      },
      error: (error: any) => {
        console.error('Error loading volunteers:', error);
      }
    });
  }

  get filteredVolunteers(): any[] {

    if (this.filterAvailable === 'AVAILABLE') {
      return this.volunteers.filter(
        volunteer => volunteer.available === true
      );
    }

    if (this.filterAvailable === 'UNAVAILABLE') {
      return this.volunteers.filter(
        volunteer => volunteer.available === false
      );
    }

    return this.volunteers;
  }
}