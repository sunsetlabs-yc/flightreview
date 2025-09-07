import { Component, OnInit } from '@angular/core';
import { ReviewService } from '../../core/services/review.service';
import { Flight } from '../../shared/models/flight';

@Component({
  selector: 'app-review-form',
  templateUrl: './review-form.component.html'
})
export class ReviewFormComponent implements OnInit {
  form = { customerName: '', customerEmail: '', flightNumber: '', rating: 1, description: '' };
  flights: Flight[] = [];
  selectedFlight: Flight | null = null;

  constructor(private reviewService: ReviewService) {}

  ngOnInit() {
    this.loadFlights();
  }

  loadFlights() {
    this.reviewService.getFlights().subscribe({
      next: (flights) => {
        this.flights = flights;
      },
      error: (err) => console.error('Error loading flights:', err)
    });
  }

  onFlightChange() {
    if (this.form.flightNumber) {
      this.selectedFlight = this.flights.find(f => f.flightNumber === this.form.flightNumber) || null;
    } else {
      this.selectedFlight = null;
    }
  }

  onSubmit() {
    this.reviewService.submitReview(this.form).subscribe({
      next: () => {
        alert('Review submitted!');
        this.form = { customerName: '', customerEmail: '', flightNumber: '', rating: 1, description: '' };
        this.selectedFlight = null;
      },
      error: (err) => console.error(err)
    });
  }
}
