import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISport } from 'app/shared/model/sport.model';

@Component({
    selector: 'jhi-sport-detail',
    templateUrl: './sport-detail.component.html'
})
export class SportDetailComponent implements OnInit {
    sport: ISport;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ sport }) => {
            this.sport = sport;
        });
    }

    previousState() {
        window.history.back();
    }
}
