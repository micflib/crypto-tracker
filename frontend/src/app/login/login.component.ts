import { Inject } from '@angular/core';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { DisplayMessage } from '../shared/models/display-message';
import { ProductPrice } from '../shared/models/product-price';
import { Subscription } from 'rxjs/Subscription';
import { FormControl } from '@angular/forms'; 111

import {
  UserService,
  AuthService,
  ApiService,
  ConfigService
} from '../service';

import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})

export class LoginComponent implements OnInit, OnDestroy {
  form: FormGroup;
  toppings = new FormControl();
  toppingList: string[] = null;
  productResponse = {};
  BFX = null;
  BNB = null;
  BTX = null;
  product = null;
  submitted = false;
  showLoading = false;
  notification: DisplayMessage;
  pprice: ProductPrice;
  returnUrl: string;
  private ngUnsubscribe: Subject<void> = new Subject<void>();

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private apiService: ApiService,
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private config: ConfigService
  ) {

  }

  ngOnInit() {
    this.showLoading = true;
    this.form = this.formBuilder.group({
    });

    return this.apiService.get(this.config.products_url)
      .delay(2000)
      .subscribe(data => {
        this.showLoading = false;
        this.toppingList = data;
      });
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  onSubmit() {
    return this.apiService.get(this.config.products_url + this.toppings.value + "/prices")
      .subscribe(data => {
        this.submitted = true;
        this.BFX = data.BFX;
        this.BNB = data.BNB;
        this.BTX = data.BTX;
        this.product = this.toppings.value;
        this.determineMinMax(data);
      },
        error => {
          this.notification = { msgType: 'info', msgBody: 'No result found for this product.' };
        });
  }

  private determineMinMax(data) {
    this.pprice = {
      class: {
        bfx: "",
        bnb: "",
        btx: ""
      },
      value: {
        bfx: data.BFX,
        bnb: data.BFX,
        btx: data.BFX
      }
    };

    var arr = [];
    arr.push(data.BFX);
    arr.push(data.BNB);
    arr.push(data.BTX);

    let min = arr[0], max = arr[0];

    for (let i = 1, len = arr.length; i < len; i++) {
      let v = arr[i];
      min = (v < min) ? v : min;
      max = (v > max) ? v : max;
    }

    if (max == data.BFX) {
      this.pprice.class.bfx = "ppgreen";
    }

    if (max == data.BNB) {
      this.pprice.class.bnb = "ppgreen";
    }

    if (max == data.BTX) {
      this.pprice.class.btx = "ppgreen";
    }

    if (min == data.BFX) {
      this.pprice.class.bfx = "ppred";
    }

    if (min == data.BNB) {
      this.pprice.class.bnb = "ppred";
    }

    if (min == data.BTX) {
      this.pprice.class.btx = "ppred";
    }
  }
}
