import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Report } from '../../services/report';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reports.html',
  styleUrl: './reports.css'
})
export class ReportsComponent implements OnInit {

  stats: any = null;
  isLoading = false;
  isExporting = false;

  constructor(
    private reportService: Report
  ) {}

  ngOnInit(): void {
    this.isLoading = true;

    this.reportService.getSystemStats().subscribe({
      next: (data: any) => {
        this.stats = data;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading reports:', error);
        this.isLoading = false;
      }
    });
  }

  exportCsv(): void {
    this.isExporting = true;
    this.reportService.exportCsv().subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `food_waste_report_${new Date().toISOString().slice(0, 10)}.csv`;
        a.click();
        window.URL.revokeObjectURL(url);
        this.isExporting = false;
      },
      error: (err) => {
        console.error('Error downloading CSV report:', err);
        this.isExporting = false;
      }
    });
  }
}
