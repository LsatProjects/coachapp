import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ISport } from 'app/shared/model/sport.model';
import { SportService } from './sport.service';

@Component({
    selector: 'jhi-sport-update',
    templateUrl: './sport-update.component.html'
})
export class SportUpdateComponent implements OnInit {
    sport: ISport;
    isSaving: boolean;

    constructor(private sportService: SportService, private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ sport }) => {
            this.sport = sport;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.sport.id !== undefined) {
            this.subscribeToSaveResponse(this.sportService.update(this.sport));
        } else {
            this.subscribeToSaveResponse(this.sportService.create(this.sport));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ISport>>) {
        result.subscribe((res: HttpResponse<ISport>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }
}
