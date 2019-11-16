import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-search-select-item-view',
  templateUrl: './search-select-item-view.component.html',
  styleUrls: ['./search-select-item-view.component.css']
})
export class SearchSelectItemViewComponent implements OnInit {

  constructor(
    private router: Router
  ) { }

  ngOnInit() {
  }

  getItemDetails() {
    this.router.navigate(['viewdetail']);
  }

}
