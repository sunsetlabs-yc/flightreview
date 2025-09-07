import { Component, OnInit } from '@angular/core';
import { ReviewService } from '../../core/services/review.service';

@Component({
  selector: 'app-review-list',
  templateUrl: './review-list.component.html'
})
export class ReviewListComponent implements OnInit {
  reviews: any[] = [];
  page = 0;
  size = 5;
  totalPages = 0;

  filters = {
    flightNumber: '',
    keyword: '',
    date: ''
  };

  constructor(private reviewService: ReviewService) {}

  ngOnInit() {
    this.loadReviews();
  }

  loadReviews() {
    this.reviewService.getPublicReviews(this.page, this.size, this.filters)
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
