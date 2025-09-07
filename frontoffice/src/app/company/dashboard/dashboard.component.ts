import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ReviewService } from '../../core/services/review.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  reviews: any[] = [];
  page = 0;
  size = 5;
  totalPages = 0;

  filters = {
    flightNumber: '',
    keyword: '',
    date: '',
    state: ''
  };

  constructor(
    private reviewService: ReviewService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadReviews();
  }

  loadReviews() {
    this.reviewService.getCompanyReviews(this.page, this.size, this.filters)
      .subscribe({
        next: (res: any) => {
          this.reviews = res.content;
          this.totalPages = res.totalPages;
        },
        error: err => console.error(err)
      });
  }

  onSearch() {
    this.page = 0;
    this.loadReviews();
  }

  viewReview(reviewId: string) {
    this.router.navigate(['/company/review', reviewId]);
  }

  nextPage() {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadReviews();
    }
  }

  prevPage() {
    if (this.page > 0) {
      this.page--;
      this.loadReviews();
    }
  }
}
