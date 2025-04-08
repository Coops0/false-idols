import { ICONS, PlayerIcon } from '@/game/player-icon.ts';

export const preloadImages = async () => {
    const icons = ICONS
        .flatMap(icon => ([
            PlayerIcon.normal(icon),
            PlayerIcon.angel(icon),
            PlayerIcon.demon(icon),
            PlayerIcon.satan(icon),
        ]))
        .map(icon => preloadImageUrl(icon));


    await Promise.all([
        ...icons,
        preloadImageImport(import('@/assets/cards/negative-card.png')),
        preloadImageImport(import('@/assets/cards/positive-card.png')),
    ]);
};

const preloadImageUrl = async (src: string) => {
    return new Promise<void>(resolve => {
        const img = new Image();
        img.src = src;
        img.onload = () => resolve();
        img.onerror = (err) => {
            console.warn('error loading image', src, err);
            resolve();
        };
    });
};

const preloadImageImport = async (i: Promise<any>) => {
    try {
        await i;
    } catch (error) {
        console.warn('error loading image', error);
    }
};