<template>
  <v-app class="bg-tiles">
    <AppNavbar />
    <v-main>
      <router-view v-slot="{ Component }">
        <transition name="page" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </v-main>

    <v-snackbar
      v-model="toast.show"
      :color="toast.color"
      :timeout="toast.timeout"
      location="top right"
      transition="slide-x-reverse-transition"
    >
      <div class="d-flex align-center" style="gap: 8px">
        <v-icon v-if="toast.color === 'success'" size="20"
          >mdi-check-circle</v-icon
        >
        <v-icon v-else-if="toast.color === 'error'" size="20"
          >mdi-alert-circle</v-icon
        >
        <v-icon v-else-if="toast.color === 'warning'" size="20"
          >mdi-alert</v-icon
        >
        <v-icon v-else size="20">mdi-information</v-icon>
        <span>{{ toast.text }}</span>
      </div>
    </v-snackbar>
  </v-app>
</template>

<script setup>
import { useToastStore } from "./stores/toast";
import AppNavbar from "./components/AppNavbar.vue";

const toast = useToastStore();
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

/* Page transitions */
.page-enter-active,
.page-leave-active {
  transition: all 0.3s ease;
}

.page-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}
</style>
