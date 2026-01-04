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
      <v-card-title class="py-3 text-h6">
        Définir un nouveau mot de passe
      </v-card-title>

      <v-divider />

      <v-card-text class="pt-6 pb-4">
        <div
          v-if="validating"
          class="d-flex flex-column align-center justify-center text-center"
        >
          <v-progress-circular indeterminate size="40" class="mb-4" />
          <div>Validation du lien de réinitialisation...</div>
        </div>
        <v-form v-else ref="form" @submit.prevent="submit">
          <v-text-field
            v-model="password"
            :type="showPassword ? 'text' : 'password'"
            label="Nouveau mot de passe"
            variant="outlined"
            :disabled="loading"
            :rules="[rules.required]"
            :append-inner-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
            @click:append-inner="showPassword = !showPassword"
            class="mb-4"
          />

          <v-text-field
            v-model="passwordConfirm"
            :type="showPasswordConfirm ? 'text' : 'password'"
            label="Confirmer le mot de passe"
            variant="outlined"
            :disabled="loading"
            :rules="[rules.required, rules.match]"
            :append-inner-icon="showPasswordConfirm ? 'mdi-eye' : 'mdi-eye-off'"
            @click:append-inner="showPasswordConfirm = !showPasswordConfirm"
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
            Mettre à jour mon mot de passe
          </v-btn>

          <v-alert
            v-if="successMessage"
            type="success"
            variant="tonal"
            class="mt-4"
          >
            {{ successMessage }}
          </v-alert>

          <v-alert
            v-if="errorMessage"
            type="error"
            variant="tonal"
            class="mt-4"
          >
            {{ errorMessage }}
          </v-alert>

          <div class="d-flex justify-center mt-4">
            <v-btn variant="text" @click="goLogin">
              Retour à la connexion
            </v-btn>
          </div>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();

const token = computed(() => {
  const t = route.query.token;
  return typeof t === "string" ? t : null;
});

const password = ref("");
const passwordConfirm = ref("");
const showPassword = ref(false);
const showPasswordConfirm = ref(false);
const loading = ref(false);
const validating = ref(true);
const successMessage = ref("");
const errorMessage = ref("");
const form = ref(null);

const rules = {
  required: (v) => !!v || "Ce champ est requis",
  match: (v) =>
    v === password.value || "Les mots de passe ne correspondent pas",
};

onMounted(async () => {
  if (!token.value) {
    router.push({ name: "forbidden" });
    return;
  }

  try {
    await auth.validateResetToken(token.value);
  } catch (e) {
    const s = e?.response?.status;
    router.push({ name: "forbidden" });
    return;
  } finally {
    validating.value = false;
  }
});

function goLogin() {
  router.push("/login");
}

function goForgot() {
  router.push("/forgot-password");
}

async function submit() {
  loading.value = true;
  successMessage.value = "";
  errorMessage.value = "";

  try {
    const valid = await form.value?.validate();
    if (!valid) return;

    const res = await auth.resetPassword(token.value, password.value);
    successMessage.value =
      res?.message || "Mot de passe réinitialisé avec succès.";

    router.push({ name: "login", query: { reset: "ok" } });
  } catch (e) {
    const s = e?.response?.status;

    if (s === 403 || s === 401) {
      router.push({ name: "forbidden" });
      return;
    }
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
