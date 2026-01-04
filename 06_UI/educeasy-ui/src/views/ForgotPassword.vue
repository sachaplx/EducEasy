<template>
  <v-container
    class="d-flex align-center justify-center"
    style="
      min-height: calc(100vh - 64px);
      max-width: 100%;
      padding-bottom: 64px;
    "
  >
    <v-card style="max-width: 560px; width: 100%">
      <v-card-title class="py-4 d-flex flex-column align-center">
        <v-icon size="40" class="mb-2" color="primary">mdi-lock-reset</v-icon>
        <div class="text-h6">Réinitialiser le mot de passe</div>
        <div class="text-body-2 text-medium-emphasis mt-1 text-center">
          Saisissez votre adresse e-mail.
          <br />
          Nous vous enverrons un lien de réinitialisation.
        </div>
      </v-card-title>

      <v-divider />

      <v-card-text class="pt-6 pb-4">
        <v-form @submit.prevent="submit" ref="form">
          <v-text-field
            v-model="identifier"
            label="Adresse e-mail"
            variant="outlined"
            :disabled="loading"
            :rules="[rules.required]"
            class="mb-4"
          />

          <v-btn
            type="submit"
            color="primary"
            :loading="loading"
            :disabled="loading"
            block
            height="48"
          >
            Envoyer le lien de réinitialisation
          </v-btn>

          <v-alert
            v-if="successMessage"
            type="success"
            variant="tonal"
            class="mt-4"
            density="comfortable"
          >
            {{ successMessage }}
          </v-alert>

          <v-alert
            v-if="errorMessage"
            type="error"
            variant="tonal"
            class="mt-4"
            density="comfortable"
          >
            {{ errorMessage }}
          </v-alert>
          <div class="text-caption text-medium-emphasis mt-4 text-center">
            Si vous ne recevez pas d’e-mail, vérifiez vos spams ou réessayez
            avec votre nom d’utilisateur.
          </div>

          <div class="d-flex justify-center mt-4">
            <v-btn variant="text" height="40" @click="goLogin">
              Retour à la connexion
            </v-btn>
          </div>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";

const router = useRouter();
const auth = useAuthStore();

const identifier = ref("");
const loading = ref(false);
const successMessage = ref("");
const errorMessage = ref("");
const form = ref(null);

const rules = {
  required: (v) => !!v || "Ce champ est requis",
};

function goLogin() {
  router.push("/login");
}

async function submit() {
  loading.value = true;
  successMessage.value = "";
  errorMessage.value = "";

  try {
    const valid = await form.value?.validate();
    if (!valid) {
      return;
    }

    const res = await auth.forgotPassword(identifier.value);
    successMessage.value =
      res?.message ||
      "Si un compte existe avec cet identifiant, un e-mail de réinitialisation a été envoyé.";
  } catch (e) {
    const s = e?.response?.status;
    const msg =
      e?.response?.data?.error || e?.response?.data?.message || e?.message;
    errorMessage.value = `Erreur (${s ?? "network"}) : ${
      msg ?? "Une erreur est survenue."
    }`;
  } finally {
    loading.value = false;
  }
}
</script>
