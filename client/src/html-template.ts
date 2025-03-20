import { ActionChoice, ActionSupplementedPlayer } from './game/messages.ts';

export class HtmlTemplate {
    private constructor() {
    }

    static login() {
        return `
            <form id="login-form" action="">
                <h1>Login</h1>
                <input type="text" placeholder="Username" id="username">
                <button type="submit">Login</button>
            </form>
        `;
    }

    static chooseAction(targets: ActionSupplementedPlayer[], options: ActionChoice[]) {
        return `
            <div id="choose-action">
                ${targets.map(HtmlTemplate.target).join('')}
                ${options.map(HtmlTemplate.choice).join('')}
            </div>
        `;
    }

    private static target(t: ActionSupplementedPlayer) {
        return `
            <div>
                <img src="${t.icon}" alt="${t.name}">
                <p>${t.name}</p>
            </div>
        `;
    }

    private static choice(option: ActionChoice) {
        return `
            <button>${option}</button>
        `;
    }
}