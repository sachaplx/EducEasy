import { defineStore } from "pinia";

export const useToastStore = defineStore("toast", {
  state: () => ({
    show: false,
    text: "",
    color: "success",
    timeout: 2500,
  }),
  actions: {
    open(text, color = "info", timeout = 2500) {
      this.text = text;
      this.color = color;
      this.timeout = timeout;
      this.show = true;
    },
    success(text, timeout = 2500) {
      this.open(text, "success", timeout);
    },
    error(text, timeout = 2500) {
      this.open(text, "error", timeout);
    },
    info(text, timeout = 2500) {
      this.open(text, "info", timeout);
    },
    warning(text, timeout = 2500) {
      this.open(text, "warning", timeout);
    },
    close() {
      this.show = false;
    },
  },
});
