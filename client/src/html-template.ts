export class HtmlTemplate {
    private constructor() {}

    static login() {
        return `
            <form id="login-form" action="">
                <h1>Login</h1>
                <input type="text" placeholder="Username" id="username">
                <button type="submit">Login</button>
            </form>
        `;
    }
}