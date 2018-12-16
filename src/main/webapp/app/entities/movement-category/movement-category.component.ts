import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IMovementCategory } from 'app/shared/model/movement-category.model';
import { Principal } from 'app/core';
import { MovementCategoryService } from './movement-category.service';

@Component({
    selector: 'jhi-movement-category',
    templateUrl: './movement-category.component.html'
})
export class MovementCategoryComponent implements OnInit, OnDestroy {
    movementCategories: IMovementCategory[];
    currentAccount: any;
    eventSubscriber: Subscription;
    currentSearch: string;

    constructor(
        private movementCategoryService: MovementCategoryService,
        private jhiAlertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private activatedRoute: ActivatedRoute,
        private principal: Principal
    ) {
        this.currentSearch =
            this.activatedRoute.snapshot && this.activatedRoute.snapshot.params['search']
                ? this.activatedRoute.snapshot.params['search']
                : '';
    }

    loadAll() {
        if (this.currentSearch) {
            this.movementCategoryService
                .search({
                    query: this.currentSearch
                })
                .subscribe(
                    (res: HttpResponse<IMovementCategory[]>) => (this.movementCategories = res.body),
                    (res: HttpErrorResponse) => this.onError(res.message)
                );
            return;
        }
        this.movementCategoryService.query().subscribe(
            (res: HttpResponse<IMovementCategory[]>) => {
                this.movementCategories = res.body;
                this.currentSearch = '';
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    search(query) {
        if (!query) {
            return this.clear();
        }
        this.currentSearch = query;
        this.loadAll();
    }

    clear() {
        this.currentSearch = '';
        this.loadAll();
    }

    ngOnInit() {
        this.loadAll();
        this.principal.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInMovementCategories();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: IMovementCategory) {
        return item.id;
    }

    registerChangeInMovementCategories() {
        this.eventSubscriber = this.eventManager.subscribe('movementCategoryListModification', response => this.loadAll());
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
