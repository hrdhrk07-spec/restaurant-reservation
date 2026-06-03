import {Page} from '@playwright/test';

export class HomePage {
    constructor(private page: Page) {
    }

    async goto() {
        await this.page.goto('/home');
    }

    async fillSearchForm(location: string, cuisineType: string, restaurantName: string) {
        await this.page.locator('#location').selectOption(location);
        await this.page.locator('#cuisineType').selectOption(cuisineType);
        await this.page.locator('#name').fill(restaurantName);
    }

    async clickSearchButton() {
        await this.page.getByRole('button', {name: '検索'}).click();
    }

}