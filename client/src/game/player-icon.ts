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

    private static normalize(icon: string): string {
        return `${__ICONS_PATH__}/${icon}.png`;
    }
}