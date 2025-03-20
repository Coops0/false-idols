export const ICONS = [
    'zebra',
    'cat',
    'mouse',
    'fox',
    'racoon',
    'rabbit',
    'dog',
    'pig',
    'bear',
    'lion'
] as const;

export class PlayerIcon {
    private static hasPreloadedIcons = false;

    private constructor() {
    }

    private static normalize(icon: string): string {
        return `/assets/icons/${icon}.png`;
    }

    static dead(icon: string): string {
        return PlayerIcon.normalize(`${icon}_dead`);
    }

    static normal(icon: string): string {
        return PlayerIcon.normalize(icon);
    }

    static angel(icon: string): string {
        return PlayerIcon.normalize(`${icon}_angel`);
    }

    static demon(icon: string): string {
        return PlayerIcon.normalize(`${icon}_demon`);
    }

    static satan(icon: string): string {
        return PlayerIcon.normalize(`${icon}_satan`);
    }

    static async preload() {
        if (this.hasPreloadedIcons) return;

        const promises = ICONS
            .flatMap(icon => ([
                PlayerIcon.normal(icon),
                PlayerIcon.dead(icon),
                PlayerIcon.angel(icon),
                PlayerIcon.demon(icon),
                PlayerIcon.satan(icon)
            ]))
            .map(icon => {
                const img = new Image();
                img.src = PlayerIcon.normalize(icon);
                return new Promise<void>(resolve => {
                    img.onload = () => resolve();
                    img.onerror = (err) => {
                        console.warn('error loading icon', icon, err);
                        resolve();
                    };
                });
            });

        try {
            await Promise.all(promises);
        } finally {
            this.hasPreloadedIcons = true;
        }
    }
}