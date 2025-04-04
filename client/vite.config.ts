import { fileURLToPath, URL } from 'node:url';

import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import tailwindcss from '@tailwindcss/vite';

// https://vite.dev/config/
export default defineConfig(({ command }) => ({
    define: {
        __WS_HOST__: JSON.stringify(command === 'build' ? 'wss://false-idols.cooperhanessian.com' : 'ws://localhost:8080'),
        __ICONS_PATH__: JSON.stringify(command === 'build' ? '/assets/icons' : '/icons')
    },
    plugins: [
        vue(),
        tailwindcss()
    ],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        },
    },
    server: {
        port: 3000,
        hmr: false,
        ws: false
    }
}));
