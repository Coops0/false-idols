export const ICONS = [
    'bear',
    'reindeer',
    'gerbil',
    'rabbit',
    'fox',
    'pig',
    'mouse',
    'dog',
    'panda',
    'koala'
] as const;

export class PlayerIcon {
    private static hasPreloadedIcons = false;

    //@formatter:off
    private constructor() {}
    //@formatter:on

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

    static dead(icon: string): string {
        return PlayerIcon.normalize(`${icon}_dead`);
    }

    static async preload() {
        if (this.hasPreloadedIcons) return;

        const promises = ICONS
            .flatMap(icon => ([
                PlayerIcon.normal(icon),
                PlayerIcon.angel(icon),
                PlayerIcon.demon(icon),
                PlayerIcon.satan(icon),
                PlayerIcon.dead(icon)
            ]))
            .map(icon => {
                const img = new Image();
                img.src = icon;
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

    private static normalize(icon: string): string {
        return `${__ICONS_PATH__}/${icon}.png`;
    }
}