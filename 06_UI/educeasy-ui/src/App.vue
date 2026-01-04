<template>
  <v-app class="bg-tiles">
    <AppNavbar />
    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup>
import AppNavbar from "./components/AppNavbar.vue";
</script>

<style>
.bg-tiles {
  --doodle-color: #dfdddd;
  --base-bg: #f7f7f7(0 74% 26%);
  min-height: 100vh;
  background-color: var(--base-bg);
  position: relative;
}

.bg-tiles::before {
  content: "";
  position: fixed;
  inset: 0;
  background: var(--doodle-color);

  -webkit-mask-image: url("/img/background-doodles.png");
  -webkit-mask-repeat: repeat;
  -webkit-mask-position: 0 0;
  -webkit-mask-size: auto;

  mask-image: url("/img/background-doodles.png");
  mask-repeat: repeat;
  mask-position: 0 0;
  mask-size: auto;

  pointer-events: none;
  z-index: 0;
}

.bg-tiles > * {
  position: relative;
  z-index: 1;
}
</style>
