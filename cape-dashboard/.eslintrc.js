module.exports = {
  root: true,
  ignorePatterns: ['projects/**/*'],
  overrides: [
    {
      env: {
        browser: true,
        node: true,
        es6: true,
      },
      files: ['*.ts'],
      parserOptions: {
        project: ['./src/tsconfig.app.json', './tsconfig.json', 'e2e/tsconfig.e2e.json'],
        createDefaultProgram: true,
        ecmaVersion: 6,
      },
      extends: [
        'eslint:recommended',
        'plugin:@typescript-eslint/recommended',
        'plugin:@typescript-eslint/recommended-requiring-type-checking',
        'plugin:@angular-eslint/recommended',
        'prettier/@typescript-eslint',
        'plugin:prettier/recommended',
      ],
      rules: {
        '@angular-eslint/component-selector': [
          'error',
          {
            type: 'element',
            style: 'kebab-case',
          },
        ],
        '@angular-eslint/directive-selector': [
          'error',
          {
            type: 'attribute',
            style: 'camelCase',
          },
        ],
      },
    },
    {
      files: ['*.html'],
      extends: ['plugin:@angular-eslint/template/recommended', 'prettier/@typescript-eslint', 'plugin:prettier/recommended'],
      rules: {},
    },
  ],
};
