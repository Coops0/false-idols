import { ICONS, PlayerIcon } from '@/game/player-icon.ts';

export const preloadImages = async () => {
    const icons = ICONS
        .flatMap(icon => ([
            PlayerIcon.normal(icon),
            PlayerIcon.angel(icon),
            PlayerIcon.demon(icon),
            PlayerIcon.satan(icon),
            PlayerIcon.dead(icon)
        ]))
        .map(icon => preloadImageUrl(icon));


    await Promise.all([
        ...icons,
        preloadImageImport('@/assets/board/board-angel.png'),
        preloadImageImport('@/assets/board/board-demon-5-6.png'),
        preloadImageImport('@/assets/board/board-demon-7-8.png'),
        preloadImageImport('@/assets/board/board-demon-9-10.png'),
        preloadImageImport('@/assets/board/board-tracker.png'),
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

const preloadImageImport = async (src: string) => {
    try {
        await import(src);
    } catch (error) {
        console.warn('error loading image', src, error);
    }
};