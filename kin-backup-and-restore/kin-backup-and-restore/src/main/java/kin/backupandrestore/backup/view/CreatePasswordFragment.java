package kin.backupandrestore.backup.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog.Builder;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import kin.backupandrestore.R;
import kin.backupandrestore.backup.presenter.CreatePasswordPresenter;
import kin.backupandrestore.backup.presenter.CreatePasswordPresenterImpl;
import kin.backupandrestore.backup.view.TextWatcherAdapter.TextChangeListener;
import kin.backupandrestore.base.KeyboardHandler;
import kin.backupandrestore.events.BroadcastManagerImpl;
import kin.backupandrestore.events.CallbackManager;
import kin.backupandrestore.events.EventDispatcherImpl;
import kin.backupandrestore.widget.PasswordEditText;
import kin.sdk.KinAccount;

public class CreatePasswordFragment extends Fragment implements CreatePasswordView {

	public static CreatePasswordFragment newInstance(@NonNull final BackupNavigator nextStepListener,
		@NonNull final KeyboardHandler keyboardHandler, @NonNull KinAccount kinAccount) {
		CreatePasswordFragment fragment = new CreatePasswordFragment();
		fragment.setNextStepListener(nextStepListener);
		fragment.setKeyboardHandler(keyboardHandler);
		fragment.setKinAccount(kinAccount);
		return fragment;
	}

	private TextWatcherAdapter confirmPassTextWatcherAdapter;
	private TextWatcherAdapter enterPassTextWatcherAdapter;

	private BackupNavigator nextStepListener;
	private KeyboardHandler keyboardHandler;
	private CreatePasswordPresenter createPasswordPresenter;
	private KinAccount kinAccount;

	private PasswordEditText enterPassEditText;
	private PasswordEditText confirmPassEditText;
	private Button nextButton;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
		@Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.backup_and_restore_fragment_backup_create_password, container, false);
		initViews(root);
		createPasswordPresenter = new CreatePasswordPresenterImpl(
			new CallbackManager(new EventDispatcherImpl(new BroadcastManagerImpl(getActivity()))), nextStepListener,
			kinAccount);
		createPasswordPresenter.onAttach(this);
		return root;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final CheckBox iUnderstandCheckbox = view.findViewById(R.id.understand_checkbox);
		iUnderstandCheckbox.setChecked(false);
	}

	private void initViews(View root) {
		enterPassEditText = root.findViewById(R.id.enter_pass_edittext);
		confirmPassEditText = root.findViewById(R.id.confirm_pass_edittext);
		nextButton = root.findViewById(R.id.next_button);

		initEnterPasswordText();
		initConfirmPassword();
		setNextButtonListener();

		final CheckBox iUnderstandCheckbox = root.findViewById(R.id.understand_checkbox);
		iUnderstandCheckbox.post(new Runnable() {
			@Override
			public void run() {
				iUnderstandCheckbox.setChecked(false);
			}
		});
		iUnderstandCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				createPasswordPresenter.iUnderstandChecked(isChecked);
			}
		});

		root.findViewById(R.id.understand_description).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				iUnderstandCheckbox.performClick();
			}
		});
	}

	private void setNextButtonListener() {
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createPasswordPresenter.nextButtonClicked(enterPassEditText.getText());
			}
		});
	}

	private void initEnterPasswordText() {
		enterPassTextWatcherAdapter = new TextWatcherAdapter(new TextChangeListener() {
			@Override
			public void afterTextChanged(Editable editable) {
				createPasswordPresenter
					.enterPasswordChanged(editable.toString(), confirmPassEditText.getText());
			}
		});
		enterPassEditText.addTextChangedListener(enterPassTextWatcherAdapter);
		enterPassEditText.setFrameBackgroundColor(R.color.backupAndRestore_black);
		openKeyboard(enterPassEditText);
	}

	private void initConfirmPassword() {
		confirmPassTextWatcherAdapter = new TextWatcherAdapter(new TextChangeListener() {
			@Override
			public void afterTextChanged(Editable editable) {
				createPasswordPresenter.confirmPasswordChanged(enterPassEditText.getText(),
					editable.toString());
			}
		});
		confirmPassEditText.addTextChangedListener(confirmPassTextWatcherAdapter);

		confirmPassEditText.setFrameBackgroundColor(R.color.backupAndRestore_black);
	}

	public void setNextStepListener(@NonNull final BackupNavigator nextStepListener) {
		this.nextStepListener = nextStepListener;
	}

	public void setKeyboardHandler(KeyboardHandler keyboardHandler) {
		this.keyboardHandler = keyboardHandler;
	}

	public void setKinAccount(KinAccount kinAccount) {
		this.kinAccount = kinAccount;
	}

	private void openKeyboard(View view) {
		keyboardHandler.openKeyboard(view);
	}

	@Override
	public void closeKeyboard() {
		keyboardHandler.closeKeyboard();
	}

	@Override
	public void resetEnterPasswordField() {
		enterPassEditText.setFrameBackgroundColor(R.color.backupAndRestore_black);
		enterPassEditText.removeError();
	}

	@Override
	public void resetConfirmPasswordField() {
		confirmPassEditText.setFrameBackgroundColor(R.color.backupAndRestore_black);
		confirmPassEditText.removeError();
	}

	@Override
	public void setEnterPasswordIsCorrect(boolean isCorrect) {
		if (isCorrect) {
			enterPassEditText.setFrameBackgroundColor(R.color.backupAndRestore_purple_blue);
			enterPassEditText.removeError();
		} else {
			enterPassEditText.setFrameBackgroundColor(R.color.backupAndRestore_red);
			enterPassEditText.showError(R.string.backup_and_restore_password_does_not_meet_req_above);
		}
	}

	@Override
	public void setConfirmPasswordIsCorrect(boolean isCorrect) {
		if (isCorrect) {
			confirmPassEditText.setFrameBackgroundColor(R.color.backupAndRestore_purple_blue);
			confirmPassEditText.removeError();
		} else {
			confirmPassEditText.setFrameBackgroundColor(R.color.backupAndRestore_red);
			confirmPassEditText.showError(R.string.backup_and_restore_password_does_not_match);
		}
	}

	@Override
	public void enableNextButton() {
		nextButton.setEnabled(true);
		nextButton.setClickable(true);
	}


	@Override
	public void disableNextButton() {
		nextButton.setEnabled(false);
		nextButton.setClickable(false);
	}

	@Override
	public void showBackupFailed() {
		new Builder(getActivity(), R.style.BackupAndRestoreAlertDialogTheme)
			.setTitle(R.string.backup_and_restore_something_went_wrong_title)
			.setMessage(R.string.backup_and_restore_we_had_some_issues_to_create_backup)
			.setPositiveButton(R.string.backup_and_restore_try_again, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					createPasswordPresenter.onRetryClicked(enterPassEditText.getText());
				}
			})
			.setNegativeButton(R.string.backup_and_restore_cancel, null)
			.create()
			.show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		confirmPassTextWatcherAdapter.release();
		enterPassTextWatcherAdapter.release();
	}
}
