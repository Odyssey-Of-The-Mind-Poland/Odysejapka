/** @type {import('tailwindcss').Config} */
export default {
  content: ['./src/**/*.{html,js,svelte,ts}'],
  theme: {
    extend: {
      colors: {
        dark: {
          50: "#e8e7e6",
          100: "#d0d0cf",
          200: "#a19f9e",
          300: "#72706f",
          400: "#4d4b4a",
          500: "#3b3a39",
          600: "#353433",
          700: "#2e2d2c",
          800: "#262524",
          900: "#1e1d1c",
          950: "#161615",
        },
        blue: {
          50: "#F5FAFD",
          100: "#EAF5FB",
          200: "#D4EAF7",
          300: "#BEE0F3",
          400: "#99CCED",
          500: "#5BC3E6",
          600: "#4DB6DF",
          700: "#3D9BC7",
          800: "#2E7FAD",
          900: "#205592",
        },
        orange: {
          50: "#FFF8EC",
          100: "#FFEDD3",
          200: "#FFE1B8",
          300: "#FFD49D",
          400: "#FFC781",
          500: "#F4961B",
          600: "#D17F17",
          700: "#A96A14",
          800: "#81520F",
          900: "#58380A",
        },
      },
    },
  },
  plugins: [
      require('@tailwindcss/forms'),
  ],
}
