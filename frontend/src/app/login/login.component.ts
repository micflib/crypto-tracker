import { Inject } from '@angular/core';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { DisplayMessage } from '../shared/models/display-message';
import { Subscription } from 'rxjs/Subscription';
import {FormControl} from '@angular/forms';111

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
  title = 'Login';
  githubLink = 'https://github.com/bfwg/angular-spring-starter';
  form: FormGroup;

  toppings = new FormControl();
  toppingList: string[] = null;

  /**
   * Boolean used in telling the UI
   * that the form has been submitted
   * and is awaiting a response
   */
  submitted = false;

  /**
   * Notification message from received
   * form request or router
   */
  notification: DisplayMessage;

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
    this.form = this.formBuilder.group({
      //username: ['', Validators.compose([Validators.required, Validators.minLength(3), Validators.maxLength(64)])],
      password: ['', Validators.compose([Validators.required, Validators.minLength(3), Validators.maxLength(32)])]
    });

    return this.apiService.get(this.config.products_url)
    .delay(3000)
    .subscribe(data =>  this.toppingList = data);
    // this.route.params
    // .takeUntil(this.ngUnsubscribe)
    // .subscribe((params: DisplayMessage) => {
    //   this.notification = params;
    // });
    // // get return url from route parameters or default to '/'
    // this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  onResetCredentials() {
    this.userService.resetCredentials()
    .takeUntil(this.ngUnsubscribe)
    .subscribe(res => {
      if (res.result === 'success') {
        alert('Password has been reset to 123 for all accounts');
      } else {
        alert('Server error');
      }
    });
  }

  repository() {
    window.location.href = this.githubLink;
  }

  onSubmit() {
    console.dir(this.toppings.value);
    this.router.navigate(['/', this.toppings.value]);
    
    
    // return this.apiService.get(this.config.products_url)
    // .delay(1000)
    // .subscribe(data =>  this.toppingList = data);

   // return this.apiService.get(this.config.products_url).map(data => this.toppingList = data);
    // /**
    //  * Innocent until proven guilty
    //  */
    // this.notification = undefined;
    // this.submitted = true;

    // this.authService.login(this.form.value)
    // // show me the animation
    // .delay(1000)
    // .subscribe(data => {
    //   this.userService.getMyInfo().subscribe();
    //   this.router.navigate([this.returnUrl]);
    // },
    // error => {
    //   this.submitted = false;
    //   this.notification = { msgType: 'error', msgBody: 'Incorrect username or password.' };
    // });

  }


}
