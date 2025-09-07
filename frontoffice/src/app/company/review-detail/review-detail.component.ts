import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ReviewService } from '../../core/services/review.service';

@Component({
  selector: 'app-review-detail',
  templateUrl: './review-detail.component.html',
  styleUrls: ['./review-detail.component.css']
})
export class ReviewDetailComponent implements OnInit {
  review: any = null;
  reviewId: string = '';
  responseText: string = '';
  newState: string = '';

  states = [
    { value: 'SUBMITTED', label: 'Submitted' },
    { value: 'TREATED', label: 'Treated' },
    { value: 'PUBLISHED', label: 'Published' },
    { value: 'REJECTED', label: 'Rejected' }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private reviewService: ReviewService
  ) {}

  ngOnInit() {
    this.reviewId = this.route.snapshot.paramMap.get('id') || '';
    this.loadReview();
  }

  loadReview() {
    this.reviewService.getReview(this.reviewId).subscribe({
      next: (review) => {
        this.review = review;
        this.responseText = review.responseText || '';
        this.newState = review.state || 'SUBMITTED';
      },
      error: err => console.error('Error loading review:', err)
    });
  }

  saveResponse() {
    this.reviewService.respondToReview(this.reviewId, this.responseText, this.newState).subscribe({
      next: () => {
        alert('Response saved successfully!');
        this.router.navigate(['/company/dashboard']);
      },
      error: err => console.error('Error saving response:', err)
    });
  }

  goBack() {
    this.router.navigate(['/company/dashboard']);
  }
}
