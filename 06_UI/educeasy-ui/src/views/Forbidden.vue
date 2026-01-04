<template>
  <v-container
    class="d-flex align-center justify-center forbidden-view"
    style="
      min-height: calc(100vh - 64px);
      max-width: 100%;
      padding-bottom: 64px;
    "
  >
    <v-card style="max-width: 560px; width: 100%">
      <v-card-title class="py-3 text-h6"> Accès refusé </v-card-title>

      <v-divider />

      <v-card-text class="pt-6 pb-4">
        <div class="d-flex flex-column align-center justify-center text-center">
          <v-icon size="56" color="warning" class="mb-4">
            mdi-lock-alert
          </v-icon>

          <div class="text-h6 mb-2">
            Vous n'avez pas l'autorisation d'accéder à cette page.
          </div>

          <div class="text-body-2 text-medium-emphasis mb-6">
            Il est possible que vous ne disposiez pas des droits suffisants ou
            que le lien ne soit plus valide.
          </div>

          <div class="d-flex flex-column flex-sm-row ga-2 w-100 justify-center">
            <v-btn
              color="primary"
              variant="flat"
              height="44"
              class="mb-2 mb-sm-0"
              @click="goHome"
            >
              Retour à l'accueil
            </v-btn>
            <v-btn
              color="primary"
              variant="outlined"
              height="44"
              @click="goLogin"
            >
              Se connecter avec un autre compte
            </v-btn>
          </div>
        </div>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { useRouter } from "vue-router";

const router = useRouter();

function goHome() {
  router.push("/");
}

function goLogin() {
  router.push("/login");
}
</script>

<style scoped>
@media (min-width: 960px) {
  .forbidden-view {
    height: 100vh;
    overflow: hidden;
  }
}
</style>
