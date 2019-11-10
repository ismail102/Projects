import { Component, OnInit } from '@angular/core';
declare var $: any

@Component({
  selector: 'app-view-detail',
  templateUrl: './view-detail.component.html',
  styleUrls: ['./view-detail.component.css']
})
export class ViewDetailComponent implements OnInit {
  quantity: number;
  sizeList: string[];
  colorList: string[];
  constructor() { }

  ngOnInit() {
    this.quantity = 1;
    this.sizeList = ['S','M','XL','XXL'];
  }

  setColor(index: number) {
    this.colorList = ['white','red','green','blue'];

    let styles = {
      'background-color': this.colorList[index]
    };
    
    return styles;
  }

  getQuantity(type: number) {
    this.sizeList = ['S','M','XL','XXL'];

    if(type==0 && this.quantity > 1) {
      this.quantity--;
    }
    else if(type==1) {
      this.quantity++;
    }
  }

}
