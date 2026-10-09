import { Component, OnInit } from '@angular/core';
import { Ngo } from '../../services/ngo';
import { Admin } from '../../services/admin';
import {DatePipe} from '@angular/common'

@Component({
  selector: 'app-ngo-management',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './ngo-management.html',
  styleUrl: './ngo-management.css'
})
export class NgoManagementComponent implements OnInit {

  ngos: any[] = [];
  filterVerified = 'ALL';

  constructor(
    private ngoService: Ngo,
    private adminService: Admin
  ) {}

  ngOnInit(): void {
    this.loadNgos();
  }

  loadNgos(): void {
    this.ngoService.getAllNgos().subscribe({
      next: (data: any) => {
        this.ngos = data;
      },
      error: (error: any) => {
        console.error('Error loading NGOs:', error);
      }
    });
  }

  get filteredNgos(): any[] {
    if (this.filterVerified === 'VERIFIED') {
      return this.ngos.filter(ngo => ngo.verified === true);
    }

    if (this.filterVerified === 'UNVERIFIED') {
      return this.ngos.filter(ngo => ngo.verified === false);
    }

    return this.ngos;
  }

  verifyNgo(ngoId: number): void {
    this.adminService.verifyNgo(ngoId).subscribe({
      next: () => {
        this.loadNgos();
      },
      error: (error: any) => {
        console.error('Error verifying NGO:', error);
      }
    });
  }
}